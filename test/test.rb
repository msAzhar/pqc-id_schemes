#system("osascript rlizard.js > output.txt")
#output = system `osascript rlizard.js`

keygen = []
encrypt = []
decrypt = []


s = 0
while true do
system `osascript kawachi.js 2>&1 | tee output2.txt`
out = File.readlines('output2.txt')
exeTime = out.grep(/: \d+/)
#p exeTime

times = []
exeTime.each do |elem|
	p elem.scan(/\d+/)
	times << elem.scan(/\d+/)
end
#p times
#p times[0]
#p times[0][0].to_i
#p times[1][0].to_i
#p times[2][0].to_i

keygen << times[0][0].to_i
encrypt << times[1][0].to_i
decrypt << times[2][0].to_i
system `rm output2.txt`

s += 1
p "SAYAC:",s
break if s == 10
end

def total(dizi)
	t = 0
	for i in dizi
		t += i
	end
	return t.to_f
end

#p "Keygen:", keygen
#p "Encrypt:", encrypt
#p "Decrypt", decrypt

#p "Toplam Keygen: ", (total(keygen)/3).round(3)

p "Ortalama Keygen:", (total(keygen)/10).round(3)
p "Ortalama Encrypt:", total(encrypt)/10
p "Ortalama Decrypt:", total(decrypt)/10







#puts "Results:"
#system("cat output.txt")
#system `cat results.txt`

#puts "Evet:"
#p system("osascript rlizard.js").scan(/: \d+/)