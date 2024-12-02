package com.coca.client.events;

import com.coca.client.enums.Shift;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShiftChangeResultEvent extends Event {
    private Shift shift;
}