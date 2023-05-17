package subway.domain;

import java.util.Objects;
import subway.exception.ErrorCode;
import subway.exception.InvalidException;

public class Station {
    private final Long id;
    private final String name;

    public Station(final Long id, final String name) {
        validate(id, name);
        this.id = id;
        this.name = name;
    }

    private void validate(final Long id, final String name) {
        validateId(id);
        validateName(name);
    }

    private void validateId(final Long id) {
        if (id <= 0) {
            throw new InvalidException(ErrorCode.INVALID_NOT_POSITIVE_ID);
        }
    }

    private void validateName(final String name) {
        if (name.isBlank()) {
            throw new InvalidException(ErrorCode.INVALID_BLANK_NAME);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Station station = (Station) o;
        return Objects.equals(id, station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
