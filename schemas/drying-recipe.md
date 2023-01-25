# Drying recipe
File path: `data/blockychef/recipes/drying/`

## File format

```json5
{
  "type": "blockychef:drying",
  "input": {
    // Use one of these two values
    "item": "minecraft:apple",
    "tag": "minecraft:apples"
  },
  "output": {
    "item": "minecraft:apple",
    "count": 1
  },
  "dryingTime": 123456,
  "experience": 1.5
}
```

## Schema
### type
- Type: ID
- Optional: No
- Description: Recipe type ID

### input
- Type: JSON Object
- Optional: No
- Description: Recipe input

### input.item
- Type: ID
- Optional: Yes when `input.tag` is defined
- Description: Input item to be used by this recipe

### input.tag
- Type: ID
- Optional: Yes when `input.item` is defined
- Description: Tag containing items which can be used as inputs for this recipe

### output
- Type: JSON Object
- Optional: No
- Description: Recipe output definition

### output.item
- Type: ID
- Optional: No
- Description: Result item of this recipe

### output.count
- Type: Integer
- Optional: Yes (default `1`)
- Description: Result item stack size

### dryingTime
- Type: Integer
- Optional: No
- Description: Time in ticks to complete this recipe

### experience
- Type: Decimal
- Optional: Yes (default `0.0`)
- Description: Amount of experience received for completion of recipe