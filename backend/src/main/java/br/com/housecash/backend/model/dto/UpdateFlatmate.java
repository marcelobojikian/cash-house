package br.com.housecash.backend.model.dto;

import javax.validation.constraints.NotEmpty;

import br.com.housecash.backend.handler.annotation.DTO;
import lombok.Data;

@DTO
@Data
public class UpdateFlatmate {

    @NotEmpty
	String nickname;

	String password;

}
