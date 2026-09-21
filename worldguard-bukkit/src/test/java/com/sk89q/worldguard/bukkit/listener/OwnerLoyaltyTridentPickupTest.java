/*
 * WorldGuard, a suite of tools for Minecraft
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldGuard team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.worldguard.bukkit.listener;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OwnerLoyaltyTridentPickupTest {
    @Test
    void permitsOnlyOwnersThrownLoyaltyTrident() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        Trident trident = mock(Trident.class);
        when(trident.getShooter()).thenReturn(player);
        PlayerPickupArrowEvent event = new PlayerPickupArrowEvent(
                player, mock(Item.class), trident);
        for (int level = 1; level <= 3; level++) {
            when(trident.getLoyaltyLevel()).thenReturn(level);
            assertTrue(EventAbstractionListener.isOwnerLoyaltyTridentReturn(event));
        }
        when(trident.getLoyaltyLevel()).thenReturn(0);
        assertFalse(EventAbstractionListener.isOwnerLoyaltyTridentReturn(event));
        when(trident.getLoyaltyLevel()).thenReturn(3);
        Player other = mock(Player.class);
        when(other.getUniqueId()).thenReturn(UUID.randomUUID());
        when(trident.getShooter()).thenReturn(other);
        assertFalse(EventAbstractionListener.isOwnerLoyaltyTridentReturn(event));
        when(trident.getShooter()).thenReturn(null);
        assertFalse(EventAbstractionListener.isOwnerLoyaltyTridentReturn(event));
    }

    @Test
    void doesNotExemptGroundItemsOrArrows() {
        Player player = mock(Player.class);
        Item item = mock(Item.class);
        assertFalse(EventAbstractionListener.isOwnerLoyaltyTridentReturn(
                new PlayerPickupItemEvent(player, item, 0)));
        assertFalse(EventAbstractionListener.isOwnerLoyaltyTridentReturn(
                new PlayerPickupArrowEvent(player, item, mock(Arrow.class))));
    }

    @Test
    void arrowPickupUsesLegacyHandlerListAndDoesNotClearCancellation() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        Trident trident = mock(Trident.class);
        when(trident.getShooter()).thenReturn(player);
        when(trident.getLoyaltyLevel()).thenReturn(3);
        PlayerPickupArrowEvent event = new PlayerPickupArrowEvent(
                player, mock(Item.class), trident);
        assertSame(PlayerPickupItemEvent.getHandlerList(), event.getHandlers());
        event.setCancelled(true);
        assertTrue(EventAbstractionListener.isOwnerLoyaltyTridentReturn(event));
        assertTrue(event.isCancelled());
    }
}
