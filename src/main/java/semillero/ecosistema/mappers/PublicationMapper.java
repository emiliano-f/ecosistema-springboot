package semillero.ecosistema.mappers;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import semillero.ecosistema.dtos.publication.PublicationDTO;
import semillero.ecosistema.dtos.publication.PublicationRequestDTO;
import semillero.ecosistema.dtos.publication.PublicationStatisticsDTO;
import semillero.ecosistema.entities.Publication;
import semillero.ecosistema.entities.PublicationImage;

import java.util.List;
import java.util.stream.Collectors;


@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public interface PublicationMapper {

    static SupplierMapper getInstance() {
        return Mappers.getMapper(SupplierMapper.class);
    }

    @Named("toEntity")
    Publication toEntity(PublicationRequestDTO source);

    @Named("toDTO")
    @Mapping(target = "images", source = "source.images", qualifiedByName = "mapImagesPaths")
    PublicationDTO toDTO(Publication source);

    @Named("toDTOsList")
    @IterableMapping(qualifiedByName = "toDTO")
    List<PublicationDTO> toDTOsList(List<Publication> source);

    @Named("toStatisticsDTO")
    @Mapping(target = "title", source = "source.title")
    @Mapping(target = "visualizationsAmount", source = "source.visualizationsAmount")
    @Mapping(target = "dateOfCreation", source = "source.dateOfCreation")
    PublicationStatisticsDTO toStatisticsDTO(Publication source);

    @Named("mapImagesPaths")
    static List<String> mapImagesPaths(List<PublicationImage> images) {
        return images.stream()
                .map(PublicationImage::getPath)
                .collect(Collectors.toList());
    }


}