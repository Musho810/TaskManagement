package am.itspace.taskmanagement.controller;
import am.itspace.taskmanagement.entity.User;
import am.itspace.taskmanagement.repository.UserRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.List;
import java.util.Optional;
@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${task.management.images.folder}")
    private String folderPath;
    @GetMapping("/user")
    public String userHome() {
        return "user";
    }
    @GetMapping("/users")
    public String users(ModelMap modelMap) {
        List<User> all = userRepository.findAll();
        modelMap.addAttribute("users", all);
        return "users";
    }
    @GetMapping("/users/add")
    public String addUserPage() {
        return "addUser";
    }
    @PostMapping("/users/add")
    public String addUser(@ModelAttribute User user,
                          @RequestParam("userImage") MultipartFile file,
                          ModelMap modelMap) throws IOException {
        Optional<User> byEmail = userRepository.findByEmail(user.getEmail());
        if (byEmail.isPresent()) {
            modelMap.addAttribute("errorMessageEmail", "Email already in use");
            return "addUser";
        }
        if (!file.isEmpty() && file.getSize() > 0) {
            if (file.getContentType() != null && !file.getContentType().contains("image")) {
                modelMap.addAttribute("errorMessageFile", "Please choose only image");
                return "addUser";
            }
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File newFile = new File(folderPath + File.separator + fileName);
            file.transferTo(newFile);
            user.setPicUrl(fileName);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/users";
    }
    @GetMapping(value = "/users/getImage", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage(@RequestParam("fileName") String fileName) throws IOException {
        InputStream inputStream = new FileInputStream(folderPath + File.separator + fileName);
        return IOUtils.toByteArray(inputStream);
    }
    @GetMapping("/users/delete")
    public String delete(@RequestParam("id") int id) {
        userRepository.deleteById(id);
        return "redirect:/users";
    }
}

