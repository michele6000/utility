package configProps;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "ConfigHolder")
public class ConfigHolderEntity {
    @Id
    private String name;
    private String value;
}
