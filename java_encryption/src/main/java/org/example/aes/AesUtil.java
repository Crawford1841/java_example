package org.example.aes;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesUtil {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String IV = "jsuhf763kish7yd9"; // 16 bytes IV
    private static final int KEY_SIZE = 128;
    private static final String CHARSET = "UTF-8";

    private static final String EN_FILE_FLAG = ".gpg";
    public static final String PUBLIC_KEY = "9940d11c1e8475709a4a3628bdf04314b2dce52d3fff406b4763510c273345eb";
    public static final String PRIVATE_KEY = "b02871a42efeeb2e9900922218c974ee6ec67b63c1ecfffe7629799a47b5216";



    public static void writeFile(String filename, String text) {
        // 使用 FileWriter 写入文件
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(Paths.get(filename)), CHARSET)) {
            writer.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(String filename, byte[] text)  {
        try (FileOutputStream  streamOut  = new FileOutputStream (filename)) {
            streamOut.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void aesEn(String key, String filename) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        key = String.format("%-16s", key);
        byte[] iv = IV.getBytes(CHARSET);

        // Generate a new AES key
        byte[] keyBytes = key.getBytes(CHARSET);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        // Initialize the cipher with the key and IV
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));

        // Encrypt the plaintext
        byte[] ciphertext = cipher.doFinal(readByteFile(filename));

        // Return the Base64-encoded ciphertext
        String plaintext = Base64.getEncoder().encodeToString(ciphertext);
        writeFile(filename + EN_FILE_FLAG, plaintext);
    }

    public static void aesDe(String key, String filename) throws Exception {
        if(!filename.endsWith(EN_FILE_FLAG)){
            System.out.println("不是加密文件，跳过");
            return;
        }
        String encrypted = readFile(filename);
        key = String.format("%-16s", key);
        byte[] iv = IV.getBytes(CHARSET);

        // Generate the AES key from the key bytes
        byte[] keyBytes = key.getBytes(CHARSET);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        // Initialize the cipher with the key and IV
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));

        // Decrypt the ciphertext
        byte[] ciphertext = Base64.getDecoder().decode(encrypted);
        byte[] plaintext = cipher.doFinal(ciphertext);
        writeFile(filename.replace(EN_FILE_FLAG,""), plaintext);
    }

    public static String readFile(String filename) throws IOException {
        StringBuilder content = new StringBuilder();
        // 使用 BufferedReader 读取文件
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(filename)),CHARSET));
        String line = null;
        while((line = bufferedReader.readLine()) != null) {
            content.append(line).append("\n");
        }
        bufferedReader.close();

        return content.toString().trim();
    }
    public static byte[] readByteFile(String filename) throws IOException {
        BufferedInputStream in = new BufferedInputStream(Files.newInputStream(new File(filename).toPath()));
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        byte[] temp = new byte[1024];
        int size = 0;
        while((size = in.read(temp)) != -1){
            out.write(temp, 0, size);
        }
        in.close();
        return out.toByteArray();
    }

    public static void main(String[] args) throws Exception {
    }
}