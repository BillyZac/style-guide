import React from 'react'
import moment from 'moment'

const Commit = ({ image_url, name, date, message, commit_url}) => (
  <div className="commit">
    <div className="avatar">
      <img src={image_url} height="50" width="50"></img>
    </div>
    <div className="committer">
      <span>{moment(date).format('MMM D, YYYY h:mm a')}</span>
      <span>{name}</span>
    </div>
    <div className="message">
      <a href={commit_url}>{message}</a>
    </div>
  </div>
)

const CommitList = ({list = []}) => {
  if (list.length > 0) {
    return (
      <div className="commit-list">
        <h2>Latest changes</h2>
        {list.slice(0, 5).map((item ={}, index) => (
          <Commit
            key={index}
            image_url={item.author ? item.author.avatar_url : ''}
            name={item.commit ? item.commit.committer.name : ''}
            date={item.commit ? item.commit.committer.date : ''}
            message={item.commit ? item.commit.message : ''}
            commit_url={item.html_url}
          />
        ))}
      </div>
    )
  }
  return <div></div>
}

export default CommitList
