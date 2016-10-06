import React from 'react'
import ReactDOM from 'react-dom'
const Hey = require('whippersnapper/build/Hey.js')
const Text = require('whippersnapper/build/Text.js')
require('zzzss/dist/css/zzzss.css')

class Thing extends React.Component {
  render() {
    return (
      <div>
        <h1>Style guide</h1>
        <h2>This is a Hey component.</h2>
        <Hey />
        <Text
          label="Planet"
          content="Jupiter"
        />
      </div>
    )
  }
}

ReactDOM.render(<Thing />, document.getElementById('app'))
