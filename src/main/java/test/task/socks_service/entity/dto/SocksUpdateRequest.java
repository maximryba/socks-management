package test.task.socks_service.entity.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.PositiveOrZero;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Schema(name = "Запрос на обновление носков")
public class SocksUpdateRequest {

    @Schema(description = "Цвет носков", example = "Красный")
    private String color;

    @Schema(description = "Процент хлопка в носках", example = "75.0")
    @PositiveOrZero(message = "Процент хлопка не может быть отрицательным числом")
    @Max(value = 100, message = "Процент хлопка не может быть  больше 100")
    private Double cottonPercentage;

}
