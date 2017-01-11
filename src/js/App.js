import React from 'react'
import Component from './components/Component.js'
import CommitList from './components/CommitList.js'
import Colors from './components/Colors.js'
import Header from 'whippersnapper/build/Header'
const Footer = require('whippersnapper/build/Footer.js')
import fetch from 'isomorphic-fetch'

const GIT_COMMIT_URL = 'https://api.github.com/repos/buildit/zzzss/commits'

import components from './data/components.js'
import colors from './data/colors.js'

const main = (
  <main>
    <Colors colors={colors} />
    {components.map((component, index) => (
      <Component
        key={`component-${index}`}
        title={component.title}
        component={component.component}
        codeSnippet={component.codeSnippet}
      />
    ))}
  </main>
)

const description =
  (<div className="intro">
    <p>Demonstrating the Whippersnapper React component library with the zzzss style library.</p>
  </div>)

class App extends React.Component {
  constructor(props) {
    super(props)
    this.state = {commitList: []}
  }

  componentDidMount() {
    fetch(GIT_COMMIT_URL)
      .then(response => response.json())
      .then(commitList => {
        this.setState({
          commitList: commitList
        })
      })
  }

  render() {
    return (
      <div>
        <Header
          logotype="Living Style Guide"
          onLogoClick={() => console.log('clik!')}
        />
        {description}
        {main}
        <CommitList list={ this.state.commitList }/>
        <Footer appVersion="1.0" />
      </div>
    )
  }
}

export default App
