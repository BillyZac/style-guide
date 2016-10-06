import React from 'react'
import ReactDOM from 'react-dom'
const Hey = require('whippersnapper/lib/react/Hey.js')
require('zzzss/dist/css/zzzss.css')

class Thing extends React.Component {
  render() {
    return (
      <div>
        <Hey />
      </div>
    )
  }
}

ReactDOM.render(<Thing />, document.getElementById('app'))
