import React from 'react'
const Text = require('whippersnapper/build/Text.js')
import Button from 'whippersnapper/build/Button.js'

const components = [
  {
    title: "Text component",
    component: <Text label="Planet" content="Jupiter" />,
    codeSnippet: '<Text label="Planet" content="Jupiter" />',
  },
  {
    title: "Button component",
    component: (
      <Button
        label="Click me"
        onClick={() => { console.log('clicked!') }}
        cssClasses="normal"
      />
    ),
    codeSnippet: `<Button
      label="Click me"
      onClick={() => { console.log('clicked!') }}
      cssClasses="normal"
    />`,
  },
  {
    title: "Warning Button",
    component: (
      <Button
        label="Watch out!"
        onClick={() => { console.log('clicked!') }}
        cssClasses="warning"
      />
    ),
    codeSnippet: `<Button
      label="Click me"
      onClick={() => { console.log('clicked!') }}
      cssClasses="warning"
    />`,
  },
]

export default components
