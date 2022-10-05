package am.itspace.taskmanagement.controller;
import am.itspace.taskmanagement.entity.Role;
import am.itspace.taskmanagement.entity.Task;
import am.itspace.taskmanagement.entity.User;
import am.itspace.taskmanagement.repository.TaskRepository;
import am.itspace.taskmanagement.repository.UserRepository;
import am.itspace.taskmanagement.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.Optional;
@Controller
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/tasks/add")
    public String addTaskPage(ModelMap modelMap) {
        List<User> users = userRepository.findAll();
        modelMap.addAttribute("users", users);
        return "addTask";
    }
    @PostMapping("/tasks/add")
    public String addTask(@ModelAttribute Task task) {
        if (task.getUser() != null && task.getUser().getId() == 0) {
            task.setUser(null);
        }
        taskRepository.save(task);
        return "redirect:/tasks";
    }
    @GetMapping("/tasks")
    public String tasksPage(ModelMap modelMap, @AuthenticationPrincipal CurrentUser currentUser) {
        Role role = currentUser.getUser().getRole();
        List<Task> tasks = role == Role.USER ?
                taskRepository.findAllByUser_Id(currentUser.getUser().getId())
                : taskRepository.findAll();
        List<User> users = userRepository.findAll();
        modelMap.addAttribute("tasks", tasks);
        modelMap.addAttribute("users", users);
        return "tasks";
    }
    @PostMapping("/tasks/changeUser")
    public String changeUser(@RequestParam("userId") int userId, @RequestParam("taskId") int taskId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        Optional<User> userOptional = userRepository.findById(userId);
        if (taskOptional.isPresent() && userOptional.isPresent()) {
            Task task = taskOptional.get();
            User user = userOptional.get();
            if (task.getUser() != user) {
                task.setUser(user);
                taskRepository.save(task);
            }
        } else if (taskOptional.isPresent() && userId == 0) {
            taskOptional.get().setUser(null);
            taskRepository.save(taskOptional.get());
        }
        return "redirect:/tasks";
    }
}