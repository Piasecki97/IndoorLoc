import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
var geometry = new THREE.BoxGeometry( 1, 9, 1 );
var material = new THREE.MeshBasicMaterial( { color: 0x00ff00 } );
var cube = new THREE.Mesh( geometry, material );
scene.add( cube );