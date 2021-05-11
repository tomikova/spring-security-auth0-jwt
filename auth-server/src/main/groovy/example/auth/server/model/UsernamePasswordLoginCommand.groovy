package example.auth.server.model

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

class UsernamePasswordLoginCommand {
    @NotEmpty
    @NotNull
    String username

    @NotEmpty
    @NotNull
    String password
}
