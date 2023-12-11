package semillero.ecosistema.mappers;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import semillero.ecosistema.dtos.category.CategoryDTO;
import semillero.ecosistema.dtos.category.CategoryResponseDTO;
import semillero.ecosistema.entities.Category;
import semillero.ecosistema.entities.CategoryImage;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CategoryMapper {

    static CategoryMapper getInstance() {
        return Mappers.getMapper(CategoryMapper.class);
    }

    @Named("toEntity")
    Category toEntity(CategoryDTO source);

    @Named("toResponseDTO")
    @Mapping(target = "image", source = "source.image", qualifiedByName = "mapImagePath")
    CategoryResponseDTO toResponseDTO(Category source);

    @Named("toResponseDTOsList")
    @IterableMapping(qualifiedByName = "toResponseDTO")
    List<CategoryResponseDTO> toResponseDTOsList(List<Category> source);

    @Named("mapImagePath")
    static String mapImagePath(CategoryImage image) {
        return image.getPath();
    }
}
