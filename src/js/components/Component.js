import React from 'react'
import Code from './Code.js'

const Component = React.createClass({
  getInitialState: function() {
      return null
  },

  render: function() {
    const {
      title,
      component,
      code,
      codeSnippet,
    } = this.props;

    return (
      <div>
        <h2>{title}</h2>
        { component }
        <Code codeSnippet={codeSnippet} />
      </div>
    )
  }
})

export default Component
