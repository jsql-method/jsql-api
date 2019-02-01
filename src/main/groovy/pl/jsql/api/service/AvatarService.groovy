package pl.jsql.api.service

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.Base64Utils
import org.springframework.web.multipart.MultipartFile
import pl.jsql.api.model.user.Avatar
import pl.jsql.api.model.user.User
import pl.jsql.api.repo.AvatarDao
import pl.jsql.api.security.service.SecurityService

import javax.imageio.ImageIO
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import static pl.jsql.api.enums.HttpMessageEnum.*


@Transactional
@Service
class AvatarService {

    @Autowired
    AvatarDao avatarDao

    @Autowired
    SecurityService securityService

    def FTP_CONFIGURATION = [host: "softwarecartoon.com", username: "stockgames-ftp", password: "s_t_o_c_k_g_a_m_e_s#123"]

    private static String UPLOADED_FOLDER = ".//"
    private static String UPLOAD_DIR = ".//jsql//images//"

    def upload(MultipartFile file) {
        FTPClient client = new FTPClient()

        byte[] bytes = file.getBytes()
        Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename())
        Files.write(path, bytes)

        String hashed = DigestUtils.sha256Hex(file.getOriginalFilename() + (System.currentTimeMillis() % 1000).toString())

        User currentUser = securityService.getCurrentAccount()

        Avatar avatar = avatarDao.findByUser(currentUser)

        if (avatar == null) avatar = new Avatar()

        avatar.name = hashed
        avatar.originalName = file.getOriginalFilename()
        avatar.user = currentUser

        String res_id = avatarDao.save(avatar).id

        client.connect(FTP_CONFIGURATION.host)
        client.login(FTP_CONFIGURATION.username, FTP_CONFIGURATION.password)
        client.setFileType(FTP.BINARY_FILE_TYPE)

        File tmp = new File(path.toString())
        FileInputStream fis = new FileInputStream(tmp)

        client.storeFile(UPLOAD_DIR + hashed + "." + FilenameUtils.getExtension(file.getOriginalFilename()), fis)
        client.logout()

        try {
            if (fis != null) {
                fis.close()
            }
            client.disconnect()
        }
        catch (IOException e) {
            e.printStackTrace()
        } finally {
            tmp.delete()
        }

        return [code: SUCCESS.getCode(), resource_id: res_id]
    }

    def getByUser() {

        FTPClient client = new FTPClient()
        InputStream inputStream = null

        User currentUser = securityService.getCurrentAccount()

        Avatar avatar = avatarDao.findByUser(currentUser)

        if (avatar == null) {
            return [image: Base64Utils.encodeToString(generateRandomImage()), imageType: MediaType.TEXT_PLAIN]
        }

        String extension = avatar.originalName.substring(avatar.originalName.lastIndexOf(".") + 1, avatar.originalName.length())

        String ftpFileName = avatar.name + '.' + extension
        String remoteFile2 = UPLOAD_DIR + ftpFileName

        byte[] image = null

        try {

            client.connect(FTP_CONFIGURATION.host)
            client.login(FTP_CONFIGURATION.username, FTP_CONFIGURATION.password)
            client.setFileType(FTP.BINARY_FILE_TYPE)

            inputStream = client.retrieveFileStream(remoteFile2)
            image = IOUtils.toByteArray(inputStream)

        } catch (IOException ex) {
            ex.printStackTrace()
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close()
                if (client.isConnected()) {
                    client.logout()
                    client.disconnect()
                }
            } catch (IOException ex) {
                ex.printStackTrace()
            }
        }

        return [image: Base64Utils.encodeToString(image), imageType: MediaType.TEXT_PLAIN]
    }

    private byte[] generateRandomImage() {

        int width = 80
        int height = 80
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        File f = new File(RandomStringUtils.randomAlphanumeric(12) + ".png")

        Random rand = new Random()
        int start = 0

        for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                int r = rand.nextInt((255 - 0) + 1) + 0
                int g = rand.nextInt((255 - 0) + 1) + 0
                int b = rand.nextInt((255 - 0) + 1) + 0
                if (x >= start) {
                    r = 10
                    g = 124
                    b = 190
                }
                int p = (r << 16) | (g << 8) | b

                img.setRGB(y, x, p)
            }
            start++
        }

        Graphics gr = img.getGraphics();
        gr.setFont(new Font("default", Font.BOLD, 16));
        gr.drawString("JSQL", 5, 60);
        gr.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ImageIO.write(img, "png", baos)
        baos.flush()
        byte[] imageInByte = baos.toByteArray()

        ImageIO.write(img, "png", f)
        FileInputStream input = new FileInputStream(f)

        MultipartFile multipartFile = new MockMultipartFile(RandomStringUtils.randomAlphanumeric(12),
                f.getName(), "image/png", IOUtils.toByteArray(input))

        upload(multipartFile)

        f.delete()
        baos.close()

        return imageInByte
    }

}
