package account.util;

import lombok.Data;
import lombok.experimental.Delegate;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * For @Valid annotation work in javax.validation in list
 */
@Data
public class ValidList<E> implements List<E> {

    @Valid
    @Delegate
    private List<E> list = new ArrayList<>();
}
