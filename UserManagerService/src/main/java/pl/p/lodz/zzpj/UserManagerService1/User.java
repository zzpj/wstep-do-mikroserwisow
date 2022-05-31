package pl.p.lodz.zzpj.UserManagerService1;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String name;
    private String role;
}
