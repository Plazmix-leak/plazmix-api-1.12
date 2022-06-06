package net.plazmix.game.mysql;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class WhereQuery {

    private final String column;
    private final Object value;
}
