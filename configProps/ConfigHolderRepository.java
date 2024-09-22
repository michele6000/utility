package configProps;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigHolderRepository extends JpaRepository<ConfigHolderEntity, String> {
}
