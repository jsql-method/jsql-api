package pl.jsql.api.controller


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import pl.jsql.api.controller.generic.ValidateController
import pl.jsql.api.dto.response.BasicResponse
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.AvatarService

@CrossOrigin
@RestController
@RequestMapping("/api/avatar")
public class  AvatarController extends ValidateController {

    @Autowired
    AvatarService avatarService

    @Security
    @PostMapping
    BasicResponse singleFileUpload(@RequestParam MultipartFile file) {
        def response = avatarService.upload(file)
        return new BasicResponse(status: 200, data: response)
    }

    @Security
    @GetMapping
    def getPreview() {
        def response = avatarService.getByUser()
        return ResponseEntity.ok().contentType(response.imageType).body(response.image)
    }

}
