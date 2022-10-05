package am.itspace.taskmanagement.repository;
import am.itspace.taskmanagement.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findAllByUser_Id(int userId);
}
