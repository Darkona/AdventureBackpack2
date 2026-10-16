/**
 * The public API of Adventure Backpack for addons: {@link adventurebackpack.api.AdventureBackpackAPI} is the entry
 * point; the wearable contracts, {@link adventurebackpack.api.FluidEffect} and
 * {@link adventurebackpack.api.IBackpackAbility} are what an addon implements. Semantic versioning, see
 * {@link adventurebackpack.api.AdventureBackpackAPI}.
 */
@API(owner = "adventurebackpack", provides = "AdventureBackpackAPI", apiVersion = ApiVersion.VALUE)
package adventurebackpack.api;

import cpw.mods.fml.common.API;
