const path = require('path')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const CleanWebpackPlugin = require('clean-webpack-plugin');

const buildFolder = 'build'

module.exports = {
  entry: './src/main.js',
  output: {
    path: path.resolve(__dirname, buildFolder),
    filename: 'index.js',
  },
  devServer: {
    inline: true,
    port: 3333
  },
  plugins: [
    new CleanWebpackPlugin([buildFolder]),
    new HtmlWebpackPlugin({
      template: './src/index.html',
    })
  ],
  module: {
    loaders: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        loader: 'babel-loader',
        query: { presets: ['es2015', 'react'] }
      },
      {
        test: /\.html$/,
        exclude: /node_modules/,
        loader: 'html',
      },
      {
        test: /\.css$/,
        loader: 'style-loader!css-loader'
      }
    ]
  }
}
