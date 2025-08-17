- Fixed meta blockstate loading errors (though they were harmless)
- Fixed model and texture loading errors (also harmless)
- Renamed several list config entries for consistency with 1.20.1, check your values
- Fixed incorrect default ore types in conversion config
- Fixed rendering of framed controller io blocks

WARNING: This release REPLACES the existing storage drawers config file.
The original file will be left unchanged, but it will not be read from.
If you've changed your config, take a look at the new storagedrawers-common-v2.toml
file and make any changes you need.  The options available are not 1:1 with the old config.