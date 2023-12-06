package semillero.ecosistema.mappers;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import semillero.ecosistema.dtos.supplier.*;
import semillero.ecosistema.entities.Supplier;
import semillero.ecosistema.entities.SupplierImage;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SupplierMapper {

    static SupplierMapper getInstance() {
        return Mappers.getMapper(SupplierMapper.class);
    }

    @Named("toEntity")
    Supplier toEntity(SupplierRequestDTO source);

    @Named("toDTO")
    @Mapping(target = "images", source = "source.images", qualifiedByName = "mapImagesPaths")
    SupplierDTO toDTO(Supplier source);

    @Named("toDTOsList")
    @IterableMapping(qualifiedByName = "toDTO")
    List<SupplierDTO> toDTOsList(List<Supplier> source);

    @Named("toFeedbackDTO")
    SupplierFeedbackDTO toFeedbackDTO(Supplier source);

    @Named("toFeedbackDTOsList")
    @IterableMapping(qualifiedByName = "toFeedbackDTO")
    List<SupplierFeedbackDTO> toFeedbackDTOsList(List<Supplier> source);

    @Named("toNameDTO")
    SupplierNameDTO toNameDTO(Supplier source);

    @Named("toNameDTOsList")
    @IterableMapping(qualifiedByName = "toNameDTO")
    List<SupplierNameDTO> toNameDTOsList(List<Supplier> source);

    @Named("toNameAndCategoryDTO")
    @Mapping(target = "category", source = "source.category.name")
    SupplierNameAndCategoryDTO toNameAndCategoryDTO(Supplier source);

    @Named("toNameAndCategoryDTOsList")
    @IterableMapping(qualifiedByName = "toNameAndCategoryDTO")
    List<SupplierNameAndCategoryDTO> toNameAndCategoryDTOsList(List<Supplier> source);

    @Named("mapImagesPaths")
    static List<String> mapImagesPaths(List<SupplierImage> images) {
        return images.stream()
                .map(SupplierImage::getPath)
                .collect(Collectors.toList());
    }
}
