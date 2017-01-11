import React from 'react'
import ColorSwatch from './ColorSwatch.js'

const Colors = ({colors}) => (
  <div className="colors">
    <h2>Colors</h2>
    <div className="color-swatches">
      {colors.map((color, index) => <ColorSwatch colorClass={color} key={index}/> )}
    </div>
  </div>
)


export default Colors
