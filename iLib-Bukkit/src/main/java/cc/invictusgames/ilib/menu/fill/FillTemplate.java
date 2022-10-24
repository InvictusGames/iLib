package cc.invictusgames.ilib.menu.fill;

import cc.invictusgames.ilib.menu.fill.impl.BorderFiller;
import cc.invictusgames.ilib.menu.fill.impl.FillFiller;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Emilxyz (langgezockt@gmail.com)
 * 17.12.2020 / 21:19
 * iLib / cc.invictusgames.ilib.menu.fill
 */

@RequiredArgsConstructor
@Getter
public enum FillTemplate {

    FILL(new FillFiller()),
    BORDER(new BorderFiller());

    private final IMenuFiller menuFiller;

}
