import React from 'react'

const ColorSwatch = ({colorClass}) => (
  <div className="color-swatch">
    <div className={`swatch ${colorClass}`}></div>
    <p className="color-name">{colorClass}</p>
  </div>
)
export default ColorSwatch
