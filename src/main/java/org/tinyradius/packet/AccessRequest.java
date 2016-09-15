/**
 * $Id: AccessRequest.java,v 1.4 2009/10/09 14:57:39 wuttke Exp $
 * Created on 08.04.2005
 *
 * @author Matthias Wuttke
 * @version $Revision: 1.4 $
 */
package org.tinyradius.packet;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

import com.NccRadius.MSCHAP;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tinyradius.attribute.IntegerAttribute;
import org.tinyradius.attribute.RadiusAttribute;
import org.tinyradius.attribute.StringAttribute;
import org.tinyradius.util.RadiusException;
import org.tinyradius.util.RadiusUtil;

/**
 * This class represents an Access-Request Radius packet.
 */
public class AccessRequest extends RadiusPacket {

    /**
     * Passphrase Authentication Protocol
     */
    public static final String AUTH_PAP = "pap";

    /**
     * Challenged Handshake Authentication Protocol
     */
    public static final String AUTH_CHAP = "chap";

    public static final String AUTH_MSCHAP2 = "mschap2";

    /**
     * Constructs an empty Access-Request packet.
     */
    public AccessRequest() {
        super();
    }

    /**
     * Constructs an Access-Request packet, sets the
     * code, identifier and adds an User-Name and an
     * User-Password attribute (PAP).
     *
     * @param userName     user name
     * @param userPassword user password
     */
    public AccessRequest(String userName, String userPassword) {
        super(ACCESS_REQUEST, getNextPacketIdentifier());
        setUserName(userName);
        setUserPassword(userPassword);
    }

    /**
     * Sets the User-Name attribute of this Access-Request.
     *
     * @param userName user name to set
     */
    public void setUserName(String userName) {
        if (userName == null)
            throw new NullPointerException("user name not set");
        if (userName.length() == 0)
            throw new IllegalArgumentException("empty user name not allowed");

        removeAttributes(USER_NAME);
        addAttribute(new StringAttribute(USER_NAME, userName));
    }

    /**
     * Sets the plain-text user password.
     *
     * @param userPassword user password to set
     */
    public void setUserPassword(String userPassword) {
        if (userPassword == null || userPassword.length() == 0)
            throw new IllegalArgumentException("password is empty");
        this.password = userPassword;
    }

    /**
     * Retrieves the plain-text user password.
     * Returns null for CHAP - use verifyPassword().
     *
     * @return user password
     * @see #verifyPassword(String)
     */
    public String getUserPassword() {
        return password;
    }

    /**
     * Retrieves the user name from the User-Name attribute.
     *
     * @return user name
     */
    public String getUserName() {
        List attrs = getAttributes(USER_NAME);
        if (attrs.size() < 1 || attrs.size() > 1)
            throw new RuntimeException("exactly one User-Name attribute required");

        RadiusAttribute ra = (RadiusAttribute) attrs.get(0);
        return ((StringAttribute) ra).getAttributeValue();
    }

    public String getServiceType() {
        List attrs = getAttributes(SERVICE_TYPE);
        if (attrs.size() < 1 || attrs.size() > 1)
            throw new RuntimeException("exactly one Service-Type attribute required");

        RadiusAttribute ra = (RadiusAttribute) attrs.get(0);
        return ((IntegerAttribute) ra).getAttributeValue();
    }

    /**
     * Returns the protocol used for encrypting the passphrase.
     *
     * @return AUTH_PAP or AUTH_CHAP
     */
    public String getAuthProtocol() {
        return authProtocol;
    }

    /**
     * Selects the protocol to use for encrypting the passphrase when
     * encoding this Radius packet.
     *
     * @param authProtocol AUTH_PAP or AUTH_CHAP
     */
    public void setAuthProtocol(String authProtocol) {
        if (authProtocol != null && (authProtocol.equals(AUTH_PAP) || authProtocol.equals(AUTH_CHAP) || authProtocol.equals(AUTH_MSCHAP2)))
            this.authProtocol = authProtocol;
        else
            throw new IllegalArgumentException("protocol must be pap, chap or ms-chap-v2");
    }

    /**
     * Verifies that the passed plain-text password matches the password
     * (hash) send with this Access-Request packet. Works with both PAP
     * and CHAP.
     *
     * @param plaintext
     * @return true if the password is valid, false otherwise
     */
    public boolean verifyPassword(String plaintext)
            throws RadiusException {
        if (plaintext == null || plaintext.length() == 0)
            throw new IllegalArgumentException("password is empty");
        if (getAuthProtocol().equals(AUTH_CHAP))
            return verifyChapPassword(plaintext);
        else if (getAuthProtocol().equals(AUTH_MSCHAP2))
            return verifyMSChapPassword(plaintext);
        else
            return getUserPassword().equals(plaintext);
    }

    /**
     * Decrypts the User-Password attribute.
     *
     * @see org.tinyradius.packet.RadiusPacket#decodeRequestAttributes(java.lang.String)
     */
    protected void decodeRequestAttributes(String sharedSecret)
            throws RadiusException {
        // detect auth protocol
        RadiusAttribute userPassword = getAttribute(USER_PASSWORD);
        RadiusAttribute chapPassword = getAttribute(CHAP_PASSWORD);
        RadiusAttribute chapChallenge = getAttribute(CHAP_CHALLENGE);
        RadiusAttribute mschapChallenge = getAttribute(311, MSCHAP_CHALLENGE);
        RadiusAttribute mschap2Response = getAttribute(311, MSCHAP2_RESPONSE);

        if (userPassword != null) {
            setAuthProtocol(AUTH_PAP);
            this.password = decodePapPassword(userPassword.getAttributeData(), RadiusUtil.getUtf8Bytes(sharedSecret));
            // copy truncated data
            userPassword.setAttributeData(RadiusUtil.getUtf8Bytes(this.password));
        } else if (chapPassword != null && chapChallenge != null) {
            setAuthProtocol(AUTH_CHAP);
            this.chapPassword = chapPassword.getAttributeData();
            this.chapChallenge = chapChallenge.getAttributeData();
        } else if (chapPassword != null) {
            setAuthProtocol(AUTH_CHAP);
            this.chapPassword = chapPassword.getAttributeData();
        } else if (mschapChallenge != null && mschap2Response != null) {
            setAuthProtocol(AUTH_MSCHAP2);
            this.mschapChallenge = mschapChallenge.getAttributeData();
            this.mschap2Response = mschap2Response.getAttributeData();
        } else
            throw new RadiusException("Access-Request: User-Password, CHAP-Password/CHAP-Challenge or MS-CHAP-Challenge missing");
    }

    /**
     * Sets and encrypts the User-Password attribute.
     *
     * @see org.tinyradius.packet.RadiusPacket#encodeRequestAttributes(java.lang.String)
     */
    protected void encodeRequestAttributes(String sharedSecret) {
        if (password == null || password.length() == 0)
            return;
        // ok for proxied packets whose CHAP password is already encrypted
        //throw new RuntimeException("no password set");

        if (getAuthProtocol().equals(AUTH_PAP)) {
            byte[] pass = encodePapPassword(RadiusUtil.getUtf8Bytes(this.password), RadiusUtil.getUtf8Bytes(sharedSecret));
            removeAttributes(USER_PASSWORD);
            addAttribute(new RadiusAttribute(USER_PASSWORD, pass));
        } else if (getAuthProtocol().equals(AUTH_CHAP)) {
            byte[] challenge = createChapChallenge();
            byte[] pass = encodeChapPassword(password, challenge);
            removeAttributes(CHAP_PASSWORD);
            removeAttributes(CHAP_CHALLENGE);
            addAttribute(new RadiusAttribute(CHAP_PASSWORD, pass));
            addAttribute(new RadiusAttribute(CHAP_CHALLENGE, challenge));
        }
    }

    /**
     * This method encodes the plaintext user password according to RFC 2865.
     *
     * @param userPass     the password to encrypt
     * @param sharedSecret shared secret
     * @return the byte array containing the encrypted password
     */
    private byte[] encodePapPassword(final byte[] userPass, byte[] sharedSecret) {
        // the password must be a multiple of 16 bytes and less than or equal
        // to 128 bytes. If it isn't a multiple of 16 bytes fill it out with zeroes
        // to make it a multiple of 16 bytes. If it is greater than 128 bytes
        // truncate it at 128.
        byte[] userPassBytes = null;
        if (userPass.length > 128) {
            userPassBytes = new byte[128];
            System.arraycopy(userPass, 0, userPassBytes, 0, 128);
        } else {
            userPassBytes = userPass;
        }

        // declare the byte array to hold the final product
        byte[] encryptedPass = null;
        if (userPassBytes.length < 128) {
            if (userPassBytes.length % 16 == 0) {
                // tt is already a multiple of 16 bytes
                encryptedPass = new byte[userPassBytes.length];
            } else {
                // make it a multiple of 16 bytes
                encryptedPass = new byte[((userPassBytes.length / 16) * 16) + 16];
            }
        } else {
            // the encrypted password must be between 16 and 128 bytes
            encryptedPass = new byte[128];
        }

        // copy the userPass into the encrypted pass and then fill it out with zeroes
        System.arraycopy(userPassBytes, 0, encryptedPass, 0, userPassBytes.length);
        for (int i = userPassBytes.length; i < encryptedPass.length; i++) {
            encryptedPass[i] = 0;
        }

        // digest shared secret and authenticator
        MessageDigest md5 = getMd5Digest();
        byte[] lastBlock = new byte[16];

        for (int i = 0; i < encryptedPass.length; i += 16) {
            md5.reset();
            md5.update(sharedSecret);
            md5.update(i == 0 ? getAuthenticator() : lastBlock);
            byte bn[] = md5.digest();

            System.arraycopy(encryptedPass, i, lastBlock, 0, 16);

            // perform the XOR as specified by RFC 2865.
            for (int j = 0; j < 16; j++)
                encryptedPass[i + j] = (byte) (bn[j] ^ encryptedPass[i + j]);
        }

        return encryptedPass;
    }

    /**
     * Decodes the passed encrypted password and returns the clear-text form.
     *
     * @param encryptedPass encrypted password
     * @param sharedSecret  shared secret
     * @return decrypted password
     */
    private String decodePapPassword(byte[] encryptedPass, byte[] sharedSecret)
            throws RadiusException {
        if (encryptedPass == null || encryptedPass.length < 16) {
            // PAP passwords require at least 16 bytes
            logger.warn("invalid Radius packet: User-Password attribute with malformed PAP password, length = " +
                    encryptedPass.length + ", but length must be greater than 15");
            throw new RadiusException("malformed User-Password attribute");
        }

        MessageDigest md5 = getMd5Digest();
        byte[] lastBlock = new byte[16];

        for (int i = 0; i < encryptedPass.length; i += 16) {
            md5.reset();
            md5.update(sharedSecret);
            md5.update(i == 0 ? getAuthenticator() : lastBlock);
            byte bn[] = md5.digest();

            System.arraycopy(encryptedPass, i, lastBlock, 0, 16);

            // perform the XOR as specified by RFC 2865.
            for (int j = 0; j < 16; j++)
                encryptedPass[i + j] = (byte) (bn[j] ^ encryptedPass[i + j]);
        }

        // remove trailing zeros
        int len = encryptedPass.length;
        while (len > 0 && encryptedPass[len - 1] == 0)
            len--;
        byte[] passtrunc = new byte[len];
        System.arraycopy(encryptedPass, 0, passtrunc, 0, len);

        // convert to string
        return RadiusUtil.getStringFromUtf8(passtrunc);
    }

    /**
     * Creates a random CHAP challenge using a secure random algorithm.
     *
     * @return 16 byte CHAP challenge
     */
    private byte[] createChapChallenge() {
        byte[] challenge = new byte[16];
        random.nextBytes(challenge);
        return challenge;
    }

    /**
     * Encodes a plain-text password using the given CHAP challenge.
     *
     * @param plaintext     plain-text password
     * @param chapChallenge CHAP challenge
     * @return CHAP-encoded password
     */
    private byte[] encodeChapPassword(String plaintext, byte[] chapChallenge) {
        // see RFC 2865 section 2.2
        byte chapIdentifier = (byte) random.nextInt(256);
        byte[] chapPassword = new byte[17];
        chapPassword[0] = chapIdentifier;

        MessageDigest md5 = getMd5Digest();
        md5.reset();
        md5.update(chapIdentifier);
        md5.update(RadiusUtil.getUtf8Bytes(plaintext));
        byte[] chapHash = md5.digest(chapChallenge);

        System.arraycopy(chapHash, 0, chapPassword, 1, 16);
        return chapPassword;
    }

    /**
     * Verifies a CHAP password against the given plaintext password.
     *
     * @return plain-text password
     */
    private boolean verifyChapPassword(String plaintext)
            throws RadiusException {

        if (plaintext == null || plaintext.length() == 0)
            throw new IllegalArgumentException("plaintext must not be empty");
//        if (chapChallenge == null || chapChallenge.length != 16)
//            throw new RadiusException("CHAP challenge must be 16 bytes");
        if (chapPassword == null || chapPassword.length != 17)
            throw new RadiusException("CHAP password must be 17 bytes");


        MessageDigest md5 = getMd5Digest();
        byte chapIdentifier = chapPassword[0];
        md5.reset();
        md5.update(chapIdentifier);
        md5.update(RadiusUtil.getUtf8Bytes(plaintext));

        if (chapChallenge == null){
            chapChallenge = getAuthenticator();
        }

        byte[] chapHash = md5.digest(chapChallenge);

        // compare
        for (int i = 0; i < 16; i++)
            if (chapHash[i] != chapPassword[i + 1])
                return false;
        return true;
    }

    private boolean verifyMSChapPassword(String plaintext) throws RadiusException {
        try {
            return MSCHAP.verifyMSCHAPv2(getUserName().getBytes(), plaintext.getBytes(), mschapChallenge, mschap2Response);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Temporary storage for the unencrypted User-Password
     * attribute.
     */
    private String password;

    /**
     * Authentication protocol for this access request.
     */
    private String authProtocol = AUTH_PAP;

    /**
     * CHAP password from a decoded CHAP Access-Request.
     */
    private byte[] chapPassword;

    /**
     * CHAP challenge from a decoded CHAP Access-Request.
     */
    private byte[] chapChallenge;

    private byte[] mschapChallenge;
    private byte[] mschap2Response;

    /**
     * Random generator
     */
    private static SecureRandom random = new SecureRandom();

    /**
     * Radius type code for Radius attribute User-Name
     */
    private static final int USER_NAME = 1;

    /**
     * Radius attribute type for User-Password attribute.
     */
    private static final int USER_PASSWORD = 2;

    /**
     * Radius attribute type for CHAP-Password attribute.
     */
    private static final int CHAP_PASSWORD = 3;

    private static final int SERVICE_TYPE = 6;

    private static final int MSCHAP_CHALLENGE = 11;
    private static final int MSCHAP2_RESPONSE = 25;

    /**
     * Radius attribute type for CHAP-Challenge attribute.
     */
    private static final int CHAP_CHALLENGE = 60;

    /**
     * Logger for logging information about malformed packets
     */
    private static Log logger = LogFactory.getLog(AccessRequest.class);

}
