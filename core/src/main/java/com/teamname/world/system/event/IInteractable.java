package com.teamname.world.system.event;

import com.teamname.world.entity.PlayerEntity;

public interface IInteractable {
    void onInteract(PlayerEntity player);
}
