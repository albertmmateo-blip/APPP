# Edit Log UI Specification

## Visual Design

### 1. Note Detail View - No Edit History
When a note has never been edited, the edit history section does not appear at all.

```
┌─────────────────────────────────────┐
│ Note Detail                         │
├─────────────────────────────────────┤
│                                     │
│ NOTE NAME                           │
│ My Important Note                   │
│                                     │
│ NOTE BODY                           │
│ This is the note content...         │
│                                     │
│ CONTACT                             │
│ John Doe                            │
│                                     │
│ CATEGORY                            │
│ Trucar                              │
│                                     │
│ CREATED                             │
│ 29/01/2024 10:30                    │
│                                     │
│ LAST MODIFIED                       │
│ 29/01/2024 10:30                    │
│                                     │
│ ┌─────────┐ ┌─────────┐            │
│ │  Edita  │ │  Back   │            │
│ └─────────┘ └─────────┘            │
│                                     │
└─────────────────────────────────────┘
```

### 2. Note Detail View - Edit History Collapsed
When a note has been edited at least once, the "View edit history" button appears between the metadata and action buttons.

```
┌─────────────────────────────────────┐
│ Note Detail                         │
├─────────────────────────────────────┤
│                                     │
│ NOTE NAME                           │
│ My Updated Note                     │
│                                     │
│ NOTE BODY                           │
│ Updated content...                  │
│                                     │
│ CONTACT                             │
│ Jane Smith                          │
│                                     │
│ CATEGORY                            │
│ Trucar                              │
│                                     │
│ CREATED                             │
│ 29/01/2024 10:30                    │
│                                     │
│ LAST MODIFIED                       │
│ 29/01/2024 14:45                    │
│                                     │
│ ┌────────────────────────────────┐  │
│ │ View edit history           ▼ │  │
│ └────────────────────────────────┘  │
│                                     │
│ ┌─────────┐ ┌─────────┐            │
│ │  Edita  │ │  Back   │            │
│ └─────────┘ └─────────┘            │
│                                     │
└─────────────────────────────────────┘
```

### 3. Note Detail View - Edit History Expanded
When the user taps "View edit history", the button changes to "Hide edit history" and the history list appears below it.

```
┌─────────────────────────────────────┐
│ Note Detail                         │
├─────────────────────────────────────┤
│                                     │
│ NOTE NAME                           │
│ My Updated Note                     │
│                                     │
│ NOTE BODY                           │
│ Updated content...                  │
│                                     │
│ CONTACT                             │
│ Jane Smith                          │
│                                     │
│ CATEGORY                            │
│ Trucar                              │
│                                     │
│ CREATED                             │
│ 29/01/2024 10:30                    │
│                                     │
│ LAST MODIFIED                       │
│ 29/01/2024 14:45                    │
│                                     │
│ ┌────────────────────────────────┐  │
│ │ Hide edit history           ▲ │  │
│ └────────────────────────────────┘  │
│                                     │
│ ┌───────────────────────────────┐   │
│ │ Contact     29/01/2024 14:45  │   │
│ │                               │   │
│ │ Changed from:                 │   │
│ │ John Doe                      │   │
│ │                               │   │
│ │ To:                           │   │
│ │ Jane Smith                    │   │
│ └───────────────────────────────┘   │
│                                     │
│ ┌───────────────────────────────┐   │
│ │ Note Body   29/01/2024 12:15  │   │
│ │                               │   │
│ │ Changed from:                 │   │
│ │ This is the original con...   │   │
│ │                               │   │
│ │ To:                           │   │
│ │ Updated content...            │   │
│ └───────────────────────────────┘   │
│                                     │
│ ┌───────────────────────────────┐   │
│ │ Note Name   29/01/2024 10:35  │   │
│ │                               │   │
│ │ Changed from:                 │   │
│ │ My Important Note             │   │
│ │                               │   │
│ │ To:                           │   │
│ │ My Updated Note               │   │
│ └───────────────────────────────┘   │
│                                     │
│ ┌─────────┐ ┌─────────┐            │
│ │  Edita  │ │  Back   │            │
│ └─────────┘ └─────────┘            │
│                                     │
└─────────────────────────────────────┘
```

## Component Details

### Toggle Button
- **Style**: Material Design 3 TextButton
- **Collapsed Text**: "View edit history"
- **Expanded Text**: "Hide edit history"
- **Icons**: 
  - Collapsed: Down arrow (▼)
  - Expanded: Up arrow (▲)
- **Icon Position**: End (right side)
- **Text Alignment**: Start (left-aligned)
- **Behavior**: Toggle between collapsed and expanded states

### History Entry Card
- **Style**: Material Design 3 CardView
- **Elevation**: 2dp
- **Corner Radius**: 8dp
- **Padding**: 12dp
- **Margin Bottom**: 8dp

### History Entry Header
- **Field Name**: 14sp, bold, primary text color
- **Timestamp**: 12sp, secondary text color, right-aligned
- **Format**: "dd/MM/yyyy HH:mm"

### History Entry Body
- **"Changed from:" Label**: 11sp, secondary text color
- **Old Value**: 13sp, primary text color, max 3 lines with ellipsis
- **"To:" Label**: 11sp, secondary text color
- **New Value**: 13sp, **primary color** (highlighted), max 3 lines with ellipsis

## Accessibility Features

### Keyboard Navigation
- Toggle button is focusable and activatable via Enter/Space
- RecyclerView items are not interactive (read-only)
- Standard Android focus order: top to bottom

### Screen Reader Support
- Toggle button announces current state
- History entries are announced with:
  - Field name
  - Timestamp
  - "Changed from [old value] to [new value]"
- Section is skipped entirely when no history exists

## Interaction States

### Button States
1. **Default**: Text button style with primary color
2. **Pressed**: Material ripple effect
3. **Focused**: Focus ring (keyboard navigation)
4. **Disabled**: N/A (button is always enabled when visible)

### Animation
- **Expand**: RecyclerView fades in with visibility change
- **Collapse**: RecyclerView fades out with visibility change
- **Icon**: Rotates smoothly when toggling (handled by icon replacement)

## Edge Cases

### Long Values
- Text is truncated with ellipsis after 3 lines
- User can see full values by editing the note

### Empty Values
- Displayed as "(empty)" in gray text
- Applies to: Contact field when null

### Multiple Simultaneous Changes
- Each field change creates a separate entry
- All entries share the same timestamp
- Entries are listed in field order:
  1. Note Name
  2. Note Body
  3. Contact
  4. Category

### First Edit
- History section appears immediately after first save
- Toggle button is automatically collapsed
- User must tap to see the single history entry

## Material Design 3 Alignment

### Colors
- Primary: Used for new values (highlighting)
- On Surface: Used for regular text
- On Surface Variant: Used for labels and timestamps
- Error: Not used in edit history

### Typography
- 14sp: Field names (bold)
- 13sp: Change values
- 12sp: Timestamps
- 11sp: Labels ("Changed from:", "To:")

### Components
- MaterialButton: Toggle button
- MaterialCardView: History entry container
- RecyclerView: History list
- TextView: All text elements

### Spacing
- Card margin: 8dp bottom
- Card padding: 12dp all sides
- Section margin: 24dp bottom
- Label margin: 2dp bottom
- Value margin: 8dp bottom (between old/new)

## Behavior Verification Checklist

- [ ] Section is completely hidden (GONE) when no edits exist
- [ ] Section appears (VISIBLE) immediately after first edit
- [ ] Toggle button shows correct text in each state
- [ ] Toggle button shows correct icon in each state
- [ ] RecyclerView is hidden when collapsed
- [ ] RecyclerView is visible when expanded
- [ ] History entries are in chronological order (newest first)
- [ ] Each entry shows correct field name, values, and timestamp
- [ ] Old and new values are clearly distinguished
- [ ] Long text is properly truncated with ellipsis
- [ ] Empty values show "(empty)" placeholder
- [ ] Button is keyboard accessible
- [ ] Button announces state to screen readers
- [ ] History content is readable by screen readers
- [ ] Material Design ripple effect works on button
- [ ] No visual glitches during expand/collapse
