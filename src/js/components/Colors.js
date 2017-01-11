import React from 'react'
import ColorSwatch from './ColorSwatch.js'

const Colors = ({colors}) => (
  <div>
    {colors.map((color, index) => <ColorSwatch colorClass={color} key={index}/> )}
  </div>
)


export default Colors
