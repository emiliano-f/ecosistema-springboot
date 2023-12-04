package semillero.ecosistema.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import semillero.ecosistema.dtos.PublicationDTO;
import semillero.ecosistema.entities.Publication;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PublicationMapper {

    public PublicationMapper INSTANCE = Mappers.getMapper(PublicationMapper.class);


    Publication toEntity(PublicationDTO source);


    Publication updateEntity(PublicationDTO source, @MappingTarget Publication target);
}