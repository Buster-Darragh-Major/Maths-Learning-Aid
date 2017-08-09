#!/bin/bash

# Variable for location of creations folder
hostFolder="$( dirname "${BASH_SOURCE[0]}" )"
creationsFolder="$( dirname "${BASH_SOURCE[0]}" )"/creations
creations="$( dirname "${BASH_SOURCE[0]}" )"/creations/*.mp4
videoFolder="$( dirname "${BASH_SOURCE[0]}" )"/creations/video
audioFolder="$( dirname "${BASH_SOURCE[0]}" )"/creations/audio


# Create necessary directories if not already existing
if [ ! -d $creationsFolder ]; then
	pushd $hostFolder &> /dev/null
	mkdir creations
	popd &> /dev/null
fi



# Function contains options for relistening and re-recording. Called by recordAudio() function.
function postRecordOptions {
	clear
	echo "Your audio has been recorded. Press (l) to relisten, (r) to re-record, or any other key to finish your creation!"
        read -n 1 -s action

        if [ "$action" = "r" ]; then
		# Enter another recordAudio function
        	recordAudio
        elif [ "$action" = "l" ]; then
                ffplay -autoexit $audioFolder/"$newCreation".wav &> /dev/null
		postRecordOptions
        fi
}



# Function contains code needed for recording audio, begins immediately counting down from 3 then records a 3 second .wav file, save to temporary creations/audio folder.
function recordAudio {
	clear
                echo "Recording begins in: 3"
                sleep 1
                clear
                echo "Recording begins in: 2"
                sleep 1
                clear
                echo "Recording begins in: 1"
                sleep 1
                clear
                echo "Recording..."

                # Create audio file
                pushd $audioFolder &> /dev/null
                ffmpeg -f alsa -i hw:0 -t 3 -y "$newCreation".wav &> /dev/null
                popd &> /dev/null
                clear

		# Play back the audio and enter into postRecordOptions()
		ffplay -autoexit $audioFolder/"$newCreation".wav &> /dev/null
		postRecordOptions
}



# Function prompts uder for function name then cretes video and audio accordingly.
function createCreation {
	echo "Enter a name for the new creation, press enter to continue:"
	echo "___________________________________________________________"
        read newCreation
	clear

	if [ -e $creationsFolder/"$newCreation".mp4 ]; then
		echo "A creation already exists under this name, please choose a unique name."
		createCreation
		return 1
	fi

	echo "The name of your new creation is $newCreation. Press (r) to rename, or hit any other key to continue."
	read -n 1 -s rename

	if [ "$rename" = "r" ]; then
		clear
		createCreation
	else
		pushd $creationsFolder &> /dev/null
		mkdir video audio
		popd &> /dev/null

		# Create video file
		pushd $videoFolder &> /dev/null
		ffmpeg -f lavfi -i color=c=blue:s=320x240:d=3.0 -vf \
"drawtext=fontfile=/path/to/font.ttf:fontsize=30: \
 fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='$newCreation'" \
"$newCreation".mp4 &> /dev/null
		popd &> /dev/null

		clear
		read -n 1 -s -r -p "You are now about to begin your audio recording. Speak clearly and try to get close to your microphone. Hit any key to proceed, you will have 3 seconds before the recording begins."
		recordAudio

		pushd $creationsFolder &> /dev/null
                ffmpeg -i "audio/$newCreation".wav -i "video/$newCreation".mp4 -vcodec copy "$newCreation".mp4 &> /dev/null
		rm -r video audio
                popd &> /dev/null

	fi
}


function displayCreations {
	echo "A list of your current creations are:"
	echo "_____________________________________"
        echo ""
        i=1
        for file in $creations; do
		prefix="$creationsFolder"
                suffix=".mp4"
		entry=${file%$suffix}
		entry=${entry#$prefix/}
                echo "  ($i)	$entry"
                i=$((i+1))
        done
        echo ""

}


function listCreations {
	if [ "$(ls -A $creationsFolder)" ]; then
		displayCreations
		read -n 1 -s -r -p "Press any key to continue"
	else
		y=0
		echo "You currently have no creations. Would you like to add some? (y/n)"
		echo "__________________________________________________________________"
		while [ $y = 0 ]; do
			read -n 1 -s answer
			clear
			case "$answer" in
				[nN])
				y=1
				;;
				[yY])
				createCreation
				y=1
				;;
			esac
		done
	fi
}



function playCreation {
	displayCreations
	echo "Select one of the above creations to play by typing its corresponding number and press enter, or type (q) then enter to return to the main menu."
	read playable

	files=($creationsFolder/*.mp4)

	if [ "$playable" = "q" ]; then
		break
	elif (( 1 > playable || playable > ${#files[@]} )); then
		clear
		echo "There is no creation with that corresponding number."
		echo "____________________________________________________"
		echo ""
		playCreation
	else
		ffplay -autoexit "${files[ $(( $playable - 1 )) ]}" &> /dev/null
	fi
}



function deleteCreation {
	displayCreations
        echo "Select one of the above creations you want to delete by typing its corresponding number and press enter, or type (q) then enter to return to the main menu."
        read deletable

	files=($creationsFolder/*.mp4)

	if [ "$deletable" = "q" ]; then
		break
	elif (( 1 > deletable || deletable > ${#files[@]} )); then
		clear
                echo "There is no creation with that corresponding number."
		echo "____________________________________________________"
                echo ""
                deleteCreation
        else
		clear
		prefix="$creationsFolder"
                suffix=".mp4"
		name=${files[ $(( $deletable - 1 )) ]}
		entry=${name%$suffix}
		entry=${entry#$prefix/}
		echo "You are attempting to delete \"$entry\", if you are sure you want to do this, press (d)."
		echo "Else, press any other key to return to the delete menu"
		read -n 1 -s sure
		if [ "$sure" = "d" ]; then
			rm -f "${files[ $(( $deletable - 1 )) ]}"
		else
			clear			
			deleteCreation
		fi
	fi
}



function quit {
	echo "Are you sure you want to quit? Press (q) again to quit or any other key to return to the main menu"
	read -n 1 -s sure
	if [ "$sure" = "q" ]; then
		exit 0
	fi
}



x=0
while [ $x = 0 ]
do
	clear

	# Display opening options menu
	echo "================================================================"
	echo "Welcome to the Maths Authoring Aid"
	echo "================================================================"
	echo ""
	echo "Please select from one of the following options:"
	echo "  (l)ist existing creations"
	echo "  (p)lay and existing creation"
	echo "  (d)elete an existing creation"
	echo "  (c)reate a new creation"
	echo "  (q)uit authoring tool"
	echo ""
	echo "Enter a selection [l/p/d/c/q]:"
	echo ""

	# Prompt user for input
	read -n 1 -s SELECTION
	clear
	case "$SELECTION" in
		[lL])
		listCreations
		;;

		[pP])
		playCreation
		;;

		[dD])
		deleteCreation
		;;

		[cC])
		createCreation
		;;

		[qQ])
		quit
		;;
	esac
done
