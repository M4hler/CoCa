package com.cthulhu.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Event {
    protected int messageCode;
}
