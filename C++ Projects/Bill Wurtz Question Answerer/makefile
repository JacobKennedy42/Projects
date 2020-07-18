dataFetcher:
	g++ -std=c++14 dataFetcher.c++ -o dataFetcher -lcurl

getData: dataFetcher
	./dataFetcher https://billwurtz.com/questions/questions.html

SupervisedNet:
	g++ -std=c++11 -I eigen-eigen-323c052e1731 SupervisedNet.c++ -o SupervisedNet

runNet: SupervisedNet
	./SupervisedNet

rmSaves:
	rm -fr saves

restoreSamples:
	cp sampleDs.html dates.html
	cp sampleQs.html questions.html
	cp sampleAs.html answers.html
	mkdir -p saves
	cp sampleH3x64L0.000500 saves/H3x64L0.000500
	cp sampleH3x128L0.000500 saves/H3x128L0.000500

clean:
	rm -f dataFetcher
	rm -f SupervisedNet
