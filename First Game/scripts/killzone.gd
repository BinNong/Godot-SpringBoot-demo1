extends Area2D

@onready var timer = $Timer
@onready var reload_timer = $ReloadTimer

var pending_reload = false

func _on_body_entered(body):
	print("You died!")
	Engine.time_scale = 0.5
	body.get_node("CollisionShape2D").queue_free()
	timer.start()


func _on_timer_timeout():
	Engine.time_scale = 1.0
	var gm = get_node("/root/Game/GameManager")
	print("Killzone got GameManager: ", gm)
	gm.end_game()
	pending_reload = true
	reload_timer.start(0.5)


func _on_reload_timer_timeout():
	if pending_reload:
		get_tree().reload_current_scene()
		pending_reload = false
