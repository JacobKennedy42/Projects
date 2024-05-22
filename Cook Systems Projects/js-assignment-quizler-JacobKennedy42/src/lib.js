import fs from 'fs'

export const chooseRandom = (array, numItems) => {
  if (array.length <= 1)
    return array;

  // TODO implement chooseRandom
  var arrayCopy = [...array];

  if (numItems >= arrayCopy.length)
    numItems = arrayCopy.length;

  var randomItems = [];
  while (numItems > 0) {
    var randomIndex = Math.floor(Math.random() * arrayCopy.length);
    randomItems.push(arrayCopy.splice(randomIndex, 1)[0]);
    --numItems;
  }
  return randomItems;
}

const makeQuestionInput = (questionNum) => {return {
  type: "input",
  name: `question-${questionNum}`,
  message: `Enter question ${questionNum}`
}}

const makeChoiceInput = (questionNum, choiceNum) => {return {
  type: "input",
  name: `question-${questionNum}-choice-${choiceNum}`,
  message: `Enter answer choice ${choiceNum} for question ${questionNum}`
}}

export const createPrompt = (params) => {
   // TODO implement createPrompt
  var numQuestions = params !== undefined && params.numQuestions !== undefined ? params.numQuestions : 1;
  var numChoices = params !== undefined && params.numChoices !== undefined ? params.numChoices : 2;

  var inputs = [];
  for (var questionNum = 1; questionNum <= numQuestions; ++questionNum) {
    inputs.push(makeQuestionInput(questionNum));
    for (var choiceNum = 1; choiceNum <= numChoices; ++choiceNum)
      inputs.push(makeChoiceInput(questionNum, choiceNum));
  }
  return inputs;
}

const makeQuestionList = (name, message) => {return {
  type: "list",
  name: name,
  message: message,
  choices: []
}}

const choiceRegex = /question-(\d)-choice-(\d)/;
const questionRegex = /question-(\d)/;
const addChoices = (questions, params) => {
  var match;
  for (const [key, value] of Object.entries(params))
    if (match = key.match(choiceRegex)) {
      const questionIndex = parseInt(match[1])-1;
      questions[questionIndex].choices.push(value);
    }
}

const makeQuestions = (params) => {
  var questions = [];
  for (const [key, value] of Object.entries(params))
    if (key.match(questionRegex) && !key.match(choiceRegex))
      questions.push(makeQuestionList(key, value))
  addChoices(questions, params);
  return questions;
}

export const createQuestions = (params = {}) => {
  // TODO implement createQuestions
  return makeQuestions(params);
}

export const readFile = path =>
  new Promise((resolve, reject) => {
    fs.readFile(path, (err, data) => (err ? reject(err) : resolve(data)))
  })

export const writeFile = (path, data) =>
  new Promise((resolve, reject) => {
    fs.writeFile(path, data, err =>
      err ? reject(err) : resolve('File saved successfully')
    )
  })
