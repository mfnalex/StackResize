# Known problems

## Stacking Tools, Weapons, Armor or other damageable items
Stacking tools, weapons, armor, or other damageable items is possible when enabled.
Players can however only stack items with the same amount of damage. For example, players can stack 5 undamaged
diamond pickaxes. They can also stack 5 diamond pickaxes were each were used exactly once. It is however not possible
to stack items with different damage values.

If a stack of more than one item gets damaged, StackResize will automatically "unstack" the whole stack. For example,
if a player has a stack of 5 undamaged pickaxes and now mines a block, they are left with 4 undamaged and 1 damaged
pickaxe.

Unfortunately it is not possible to stack damaged tools by moving them with shift-click into another inventory.
Players can still stack them by hand. For undamaged items, moving with shift-click will work just fine, though.