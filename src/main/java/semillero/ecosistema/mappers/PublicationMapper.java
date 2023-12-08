package semillero.ecosistema.mappers;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import semillero.ecosistema.dtos.PublicationDTO;
import semillero.ecosistema.entities.Publication;
import semillero.ecosistema.entities.PublicationImage;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PublicationMapper {

    public PublicationMapper INSTANCE = Mappers.getMapper(PublicationMapper.class);


    Publication toEntity(PublicationDTO source);

    @Mapping(target = "imageUrls", source = "source.images", qualifiedByName = "mapImagesPaths")
    PublicationDTO toDTO(Publication source);

    @Named("mapImagesPaths")
    static List<String> mapImagesPaths(List<PublicationImage> images) {
        return images.stream()
                .map(PublicationImage::getPath)
                .collect(Collectors.toList());
    }

    Publication updateEntity(PublicationDTO source, @MappingTarget Publication target);
}