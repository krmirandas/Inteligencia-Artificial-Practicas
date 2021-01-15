# -*- coding: utf-8 -*-
import sys, ast, copy
from decimal import Decimal
from operator import itemgetter

# Tomamos el estado pasado como parámetro
inputList = []

# variable auxiliar para saber si es estado final
gb = 0

# variable donde guardaremos el estado 'mejor opción'
opcion = []

# Método para imprimir el estado que se pasa como parámetro
def pinta(estado):
	for row in estado:
		print row[0] + ' ' + row[1] + ' ' + row[2]

# Método que verifica si un estado es valido
# Regresa el valor de siguiente jugador (marca)
# Regresa None si no es un estado valido
def esValido(inputList):
	# variables auxiliares que cuentan el número de apariciones de cada valor (g = guion bajo)
	x = 0
	o = 0
	g = 0
	for row in inputList:
		for column in row:
			if column == 'X':
				x = x + 1
			elif column == 'O':
				o = o + 1
			else:
				g = g + 1
	global gb
	gb = g
	if g == 0:
		# es estado final
		return 'F'
	if x == (o-1):
		# el siguiente en tirar es X
		return 'X'
	elif o == (x-1):
		# el siguiente en tirar es O
		return 'O'
	elif o == x:
		# el siguiente en tirar es X (por omisión suponemos el primero en tirar es X)
		return'X'
	return None

# Genera los sucesores del estado (inputList) con el siguiente tiro (marca)
def generaSucesores(inputList, marca):
	sucesores = []
	for i in xrange(0,3):
		for j in xrange(0,3):
			estado = inputList[:]
			if estado[i][j] == '_':
				sucesor = [[0,0,0],[0,0,0],[0,0,0]]
				sucesor[0] = estado[0][:]
				sucesor[1] = estado[1][:]
				sucesor[2] = estado[2][:]
				sucesor[i][j] = marca
				sucesores.append(sucesor)
	return sucesores

# Método auxiliar para saber si hay ganador en el estado (inputList)
# regresa el valor (marca) del ganador
# regresa None si no hay ganador
def hayGanador(inputList):
	if inputList[0][0] == inputList[0][1] == inputList[0][2] != '_':
		return inputList[0][0]
	if inputList[1][0] == inputList[1][1] == inputList[1][2] != '_':
		return inputList[1][0]
	if inputList[2][0] == inputList[2][1] == inputList[2][2] != '_':
		return inputList[2][0]
	if inputList[0][0] == inputList[1][0] == inputList[2][0] != '_':
		return inputList[0][0]
	if inputList[0][1] == inputList[1][1] == inputList[2][1] != '_':
		return inputList[0][1]
	if inputList[0][2] == inputList[1][2] == inputList[2][2] != '_':
		return inputList[0][2]
	if inputList[0][0] == inputList[1][1] == inputList[2][2] != '_':
		return inputList[0][0]
	if inputList[0][2] == inputList[1][1] == inputList[2][0] != '_':
		return inputList[0][2]
	return None

# variables auxiliares: Infinito y -Infinito
pos_inf = Decimal('Infinity')
neg_inf = Decimal('-Infinity')

# Método que evalua max
# estado actual
# profundidad para llegar a este estado
# marca: el jugador que va a tirar
def max_valor(estado, profundidad, marca):
	# vemos si hay ganador
	ganador = hayGanador(estado)
	if ganador:
		#pinta(estado)
		if ganador == marca:
			# si el que va a tirar gana le asignamos su valor
			# 100 - profundidad para tener prioridad cuando gana más rápido
			return 100 - profundidad
		# si no le decimos a min que esta una mala opción
		return profundidad - 100
	if esValido(estado) == 'F':
		# No hay ganador y es un estado final := hay empate
		return 0
	# asignamos a v = -Infinito
	v = neg_inf
	# obtenemos los sucesores del estado con tirada del jugador 'marca'
	sucesores = generaSucesores(estado, marca)
	# cambiamos el valor de marca para el siguiente tirador
	if marca == 'X':
		marca = 'O'
	else:
		marca = 'X'
	# variable auxiliar para guardar todas las opciones
	opciones = []
	# verificamos cada sucesor con min
	for sucesor in sucesores:
		# variable auxliar para guardar el valor del sucesor al aplicar min
		sv = min_valor(sucesor, profundidad+1, marca)
		# guardamos el sucesor con su valor
		opciones.append([sucesor, sv])
		# si hay una mejor opción lo guardamos en la variable auxiliar best
		if sv > v:
			best = sucesor
		# asignamos a v su nuevo 'mejor valor'
		v = max(v, sv)
	# actualizamos la mejor opción
	global opcion
	opcion = best
	#print 'max_valor'
	#pinta(estado)
	#print v
	#print opcion
	return v

# Método que evalua min
# estado actual
# profundidad para llegar a este estado
# marca: el jugador que va a tirar
def min_valor(estado, profundidad, marca):
	# vemos si hay ganador
	ganador = hayGanador(estado)
	if ganador:
		if ganador == marca:
			# si el que va a tirar gano le asignamos su valor
			# profundidad - 100 para tener menos prioridad cuando pierde más rápido
			# el jugador que va a tirar en el estado original
			return profundidad - 100
		# si no le decimos a max que esta una buena opción
		return 100 - profundidad
	if esValido(estado) == 'F':
		# No hay ganador y es un estado final := hay empate
		return 0
	# asignamos a v = Infinito
	v = pos_inf
	# obtenemos los sucesores del estado con tirada del jugador 'marca'
	sucesores = generaSucesores(estado, marca)
	# variable auxiliar para guardar todas las opciones
	opciones = []
	if marca == 'X':
		marca = 'O'
	else:
		marca = 'X'
	# verificamos cada sucesor con max
	for sucesor in sucesores:
		# variable auxliar para guardar el valor del sucesor al aplicar max
		sv = max_valor(sucesor, profundidad+1, marca)
		# guardamos el sucesor con su valor
		opciones.append([sucesor, sv])
		# si hay una mejor opción lo guardamos en la variable auxiliar best
		if v > sv:
			best = sucesor
		# asignamos a v su nuevo 'mejor valor'
		v = min(v, sv)
	global opcion
	# actualizamos la mejor opción
	opcion = best
	#print 'min_valor'
	#pinta(estado)
	#print v
	#print opcion
	return v

try:
	inputList = ast.literal_eval(sys.argv[1])
	sig = esValido(inputList)
	gano = hayGanador(inputList)
	if sig:
		print '\nEstado de entrada:\n'
		pinta(inputList)
		print ' '
		if gano:
			print 'Ya hay ganador (%s), no hay opciones' % gano
		elif sig != 'F':
			max_valor(inputList, 0, sig)
			print 'Estado mejor opcion para ' + sig + ':\n'
			pinta(opcion)
		else:
			print "Es un estado final, no hay opciones"
	else:
		print '\nNo es un estado valido, ejemplo:\n'
		print '''"[['_', '_', 'X'], ['_', '_', 'O'],['X', 'O', 'X']]"\n'''
except Exception, e:
	print '\nSe esperaba como parámetro el estado valido de un gato\nEjemplo:\n'
	print '''python Minimax.py  "[['_', '_', 'X'], ['_', '_', 'O'],['X', 'O', 'X']]"\n'''