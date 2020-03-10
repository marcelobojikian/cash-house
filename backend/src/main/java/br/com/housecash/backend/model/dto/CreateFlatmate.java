package br.com.housecash.backend.model.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import br.com.housecash.backend.handler.annotation.DTO;
import lombok.Data;

@DTO
@Data
public class CreateFlatmate {

    @NotNull
    @NotEmpty
	private String email;

	private String nickname;

    @NotNull
    @NotEmpty
	private String password;

}
