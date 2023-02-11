package nextstep.subway.domain;

import nextstep.subway.common.ErrorMessage;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Embedded
    private SectionCollection sectionCollection = new SectionCollection();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Section> getSections() {
        return sectionCollection.getSections();
    }

    public void addSections(Section section) {
        sectionCollection.addSection(section);
    }

    public List<Station> getStations() {

        return sectionCollection.getStations();
    }

    public void removeStation(Station station) {
        List<Section> sections = sectionCollection.getSections();
        int index = sections.size() - 1;

        if (index < 1) {
            throw new IllegalStateException(ErrorMessage.ENOUGH_NOT_SECTION_SIZE.toString());
        }
        if (!sections.get(index).getDownStation().equals(station)) {
            throw new IllegalArgumentException(ErrorMessage.ENOUGH_REMOVE_DOWN.toString());
        }

        sections.remove(index);
    }
}
