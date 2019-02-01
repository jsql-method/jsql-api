package pl.jsql.api.controller.user

import com.google.gson.Gson
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.AvatarService

@CrossOrigin
@RestController
@RequestMapping("/api/avatar")
class AvatarController {

    @Autowired
    AvatarService avatarService

    @Security
    @PostMapping
    def singleFileUpload(@RequestHeader(value = "Session", required = false) String session, @RequestParam MultipartFile file) {

        def response = avatarService.upload(file)

        return new ResponseEntity(response, HttpStatus.OK)
    }

    @Security
    @GetMapping
    def getPreview(@RequestHeader(value = "Session", required = false) String session) {

        def response = avatarService.getByUser()

        return ResponseEntity.ok().contentType(response.imageType).body(response.image)
    }
}
