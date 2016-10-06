import React from 'react'
import ReactDOM from 'react-dom'
const Hey = require('whippersnapper/lib/react/Hey.js')
require('zzzss/dist/css/zzzss.css')

class Thing extends React.Component {
  render() {
    return (
      <div>
        <h1>Style guide</h1>
        <p>This is a Hey component.</p>
        <p>To make one for yourself import the component and style, like so:</p>
        <pre>const Hey = require('whippersnapper/lib/react/Hey.js')</pre>
        <pre>require('zzzss/dist/css/zzzss.css')</pre>
        <Hey />
      </div>
    )
  }
}

ReactDOM.render(<Thing />, document.getElementById('app'))
