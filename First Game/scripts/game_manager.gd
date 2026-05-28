extends Node

var score = 0
var player_id = 1
var auth_token = ""
var session_id = ""
var config_path = "res://player_config.cfg"

@onready var score_label = $ScoreLabel
@onready var http_request = $HTTPRequest

const API_BASE = "http://localhost:8080/api"
const HEARTBEAT_INTERVAL = 15.0

var pending_request_type = ""
var pending_session_id = ""
var heartbeat_timer: Timer

func _notification(what):
	if what == NOTIFICATION_EXIT_TREE:
		print("GameManager exiting tree, calling end_game for cleanup")
		if heartbeat_timer:
			heartbeat_timer.stop()
		end_game()

func _ready():
	_load_config()
	http_request.request_completed.connect(_on_request_completed)
	_setup_heartbeat_timer()
	start_game()

func _load_config():
	var config = ConfigFile.new()
	var err = config.load(config_path)
	if err == OK:
		player_id = config.get_value("player", "player_id", 1)
		auth_token = config.get_value("player", "auth_token", "")
		print("Loaded config: player_id=", player_id)
	else:
		print("Failed to load config file: ", err)

func start_game():
	if auth_token == "":
		print("No auth token, cannot start game session")
		return
	pending_request_type = "start"
	var url = API_BASE + "/game/start"
	var headers = [
		"Content-Type: application/json",
		"Authorization: Bearer " + auth_token
	]
	var body = JSON.stringify({"playerId": player_id})
	http_request.request(url, headers, HTTPClient.METHOD_POST, body)

func _setup_heartbeat_timer():
	heartbeat_timer = Timer.new()
	heartbeat_timer.wait_time = HEARTBEAT_INTERVAL
	heartbeat_timer.timeout.connect(_on_heartbeat_timeout)
	add_child(heartbeat_timer)
	heartbeat_timer.start()

func _on_heartbeat_timeout():
	if session_id != "":
		send_heartbeat()

func send_heartbeat():
	if auth_token == "" or session_id == "":
		return
	var url = API_BASE + "/game/heartbeat"
	var headers = [
		"Content-Type: application/json",
		"Authorization: Bearer " + auth_token
	]
	var body = JSON.stringify({"sessionId": session_id})
	http_request.request(url, headers, HTTPClient.METHOD_POST, body)

func add_point():
	score += 1
	score_label.text = "You collected " + str(score) + " coins."
	submit_score(1)

func submit_score(delta: int):
	if auth_token == "":
		print("No auth token, skipping score submission")
		return
	if session_id == "":
		print("No session id, skipping score submission")
		return
	if http_request.get_http_client_status() == HTTPClient.STATUS_REQUESTING:
		http_request.cancel_request()

	var url = API_BASE + "/game/score"
	var headers = [
		"Content-Type: application/json",
		"Authorization: Bearer " + auth_token
	]
	var body = JSON.stringify({"sessionId": session_id, "scoreDelta": delta})
	http_request.request(url, headers, HTTPClient.METHOD_POST, body)

func end_game():
	print("end_game called, auth_token empty=", auth_token == "", ", session_id='", session_id, "'")
	if auth_token == "" or session_id == "":
		print("end_game skipped: auth or session empty")
		return
	# 取消正在进行的请求，确保 end_game 请求能发送
	http_request.cancel_request()

	var url = API_BASE + "/game/end"
	var headers = [
		"Content-Type: application/json",
		"Authorization: Bearer " + auth_token
	]
	var body = JSON.stringify({"sessionId": session_id})
	print("Sending end_game request: POST ", url)
	print("Headers: ", headers)
	print("Body: ", body)
	var err = http_request.request(url, headers, HTTPClient.METHOD_POST, body)
	print("request() returned error code: ", err)
	session_id = ""

func _on_request_completed(result, response_code, headers, body):
	print("Request completed: result=", result, " code=", response_code, " pending=", pending_request_type)
	if result == HTTPRequest.RESULT_SUCCESS and response_code == 200:
		if pending_request_type == "start":
			var json = JSON.new()
			json.parse(body.get_string_from_utf8())
			var response = json.get_data()
			print("Start response: ", response)
			if response and "sessionId" in response:
				session_id = response["sessionId"]
				print("Session ID set to: ", session_id)
			pending_request_type = ""
		else:
			print("Request completed successfully")
	else:
		print("Request failed, result: ", result, " response_code: ", response_code)
		pending_request_type = ""
