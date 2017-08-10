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
	echo "-----------------------------------------------------------"
	echo "Your audio has been recorded. Press (l) to relisten, (r) to"
	echo "re-record, or any other key to finish your creation!"
	echo "-----------------------------------------------------------"
        read -n 1 -s action

        if [ "$action" = "r" ]; then
		# Enter another recordAudio function
        	recordAudio
        elif [ "$action" = "l" ]; then
                ffplay -autoexit $audioFolder/"$newCreation".wav &> /dev/null
		postRecordOptions
        fi
}



# Function contains code needed for recording audio, recording begins immediately then records a 3 second .wav file, save to temporary creations/audio folder.
function recordAudio {
	clear
		# Here I was advised to play it safe and not include a count down until recording begins, because of the specification stating recording must begin immediately. I was however told I could leave it commented out as a reference to a nice implementation idea.
                #echo "Recording begins in: 3"
                #sleep 1
                #clear
                #echo "Recording begins in: 2"
                #sleep 1
                #clear
                #echo "Recording begins in: 1"
                #sleep 1
                #clear
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
	echo "-----------------------------------------------------------"
	echo "Enter a name for the new creation, press enter to continue:"
	echo "-----------------------------------------------------------"
        read newCreation
	clear

	overwrite="false"

	# If mp4 file under the same name exists in creations directory promp user to enter different name
	if [ -e $creationsFolder/"$newCreation".mp4 ]; then
		echo "-----------------------------------------------------------"
		echo "A creation already exists under this name, do you wish to"
		echo "overwrite \"$newCreation\"? (y/n)"
		echo "-----------------------------------------------------------"
		y=0
		while [ $y = 0 ]; do
			read -n 1 -s answer
			clear
			case "$answer" in
				[nN])
				createCreation
				return 1
				;;
				[yY])
				overwrite="true"
				y=1
				;;
			esac
		done
	fi

	echo "-----------------------------------------------------------"
	echo "The name of your new creation is $newCreation. Press"
	echo "(r) to rename, or hit any other key to continue."
	echo "-----------------------------------------------------------"
	read -n 1 -s rename

	# If user wishes to enter a different name then enter another createCreation function, else initiate creating the creation.
	if [ "$rename" = "r" ]; then
		clear
		createCreation
	else
		# Make temporary video and audio folders to store raw unfinished video and audio data.
		pushd $creationsFolder &> /dev/null
		mkdir -p video audio
		popd &> /dev/null

		# Create video file
		pushd $videoFolder &> /dev/null
		ffmpeg -f lavfi -i color=c=blue:s=320x240:d=3.0 -vf \
"drawtext=fontfile=/path/to/font.ttf:fontsize=30: \
 fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='$newCreation'" \
"$newCreation".mp4 &> /dev/null
		popd &> /dev/null

		# Prompt user for when they are ready to begin recording audio, when key pressed enter recordAudio function.
		clear
		echo "-----------------------------------------------------------"
		echo "You are now about to begin your audio recording. Speak"
		echo "clearly and try to get close to your microphone. Hit any key"
		echo "to proceed, recording will begin immediately."
		echo "-----------------------------------------------------------"
		read -n 1 -s -r -p
		recordAudio

		# Combine audio and video folders into singe creation file in creations directory, remove audio and video directories along with their contents.
		pushd $creationsFolder &> /dev/null
		if [ "$overwrite" = "true" ]; then
			rm "$newCreation".mp4 &> /dev/null
		fi
                ffmpeg -i "audio/$newCreation".wav -i "video/$newCreation".mp4 -vcodec copy "$newCreation".mp4 &> /dev/null
		rm -r video audio
                popd &> /dev/null

	fi
}




# Function called by implementations requiring a ist of the users current creations to be shown along with numbers. Current users are list, play and delete.
function displayCreations {
	echo "-----------------------------------------------------------"
	echo "A list of your current creations are:"
	echo "-----------------------------------------------------------"
        echo ""
	# Loop through and display creations found in creations directory.
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




function emptyCreations {
	y=0
	echo "-----------------------------------------------------------"
	echo "You currently have no creations. Would you like to add some?"
	echo "(y/n)"
	echo "-----------------------------------------------------------"
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
}




# Function that calls disPlay creations to show the user a current list of their creations. If user has no creations, prompt them to create some, in which case enter createCreation function
function listCreations {
	if [ "$(ls -A $creationsFolder)" ]; then
		displayCreations
		echo "-----------------------------------------------------------"
		read -n 1 -s -r -p "Press any key to continue"
	else
		emptyCreations
		return 1
	fi
}



# Function that lists current creation files, and prompts user to enter a number corresponding to the creation they'd like to play.
function playCreation {
	if [ "$(ls -A $creationsFolder)" ]; then
		displayCreations
		echo "Select one of the above creations to play by typing its"
		echo "corresponding number and press enter, or type (q) then"
		echo "enter to return to the main menu."
		echo "-----------------------------------------------------------"
		read playable

		# Create array of all .mp4 files in creations directory
		files=($creationsFolder/*.mp4)

		# If user inputs incorrect character not in array, prompt warning message and enter further playCreation function, else play desired creation.
		if [ "$playable" = "q" ]; then
			return 1
		elif (( 1 > playable || playable > ${#files[@]} )); then
			clear
			echo "-----------------------------------------------------------"
			echo "-----------------------------------------------------------"
			echo "There is no creation with that corresponding number."
			echo "-----------------------------------------------------------"
			playCreation
		else
			ffplay -autoexit "${files[ $(( $playable - 1 )) ]}" &> /dev/null
		fi
	else
		emptyCreations
		return 1
	fi
}



# Function that lists current creation files, and prompts user to enter a number corresponding to the creation they'd like to delete.
function deleteCreation {
	if [ "$(ls -A $creationsFolder)" ]; then
		displayCreations
		echo "-----------------------------------------------------------"
        	echo "Select one of the above creations you want to delete by"
		echo "typing its corresponding number and press enter, or type (q)"
		echo "then enter to return to the main menu."
		echo "-----------------------------------------------------------"
        	read deletable

		# Create array of all .mp4 files in creations directory
		files=($creationsFolder/*.mp4)

		# If user inputs incorrect character not in array, prompt warning message and enter further deleteCreation function, else prompt user with secondary delete message and delete required file if both prompts are satisfied.
		if [ "$deletable" = "q" ]; then
			break
		elif (( 1 > deletable || deletable > ${#files[@]} )); then
			clear
			echo "-----------------------------------------------------------"
			echo "-----------------------------------------------------------"
        	        echo "There is no creation with that corresponding number."
			echo "-----------------------------------------------------------"
        	        deleteCreation
        	else
			clear
			prefix="$creationsFolder"
        	        suffix=".mp4"
			name=${files[ $(( $deletable - 1 )) ]}
			entry=${name%$suffix}
			entry=${entry#$prefix/}
			echo "-----------------------------------------------------------"
			echo "-----------------------------------------------------------"
			echo "You are attempting to delete \"$entry\", you will be unable"
			echo "to retrieve this file. If you are sure you want to do this,"
			echo "press (d). Else, press any other key to return to the"
			echo "delete menu"
			echo "-----------------------------------------------------------"
			echo "-----------------------------------------------------------"
			read -n 1 -s sure
			if [ "$sure" = "d" ]; then
				rm -f "${files[ $(( $deletable - 1 )) ]}"
			else
				clear			
				deleteCreation
			fi
		fi
	else
		emptyCreations
		return 1
	fi
	
}



# Function that propmts uder with message asking if theyre sure they want to quit, exit 0 if so.
function quit {
	echo "-----------------------------------------------------------"
	echo "Are you sure you want to quit? Press (q) again to quit or"
	echo "any other key to return to the main menu"
	echo "-----------------------------------------------------------"
	read -n 1 -s sure
	if [ "$sure" = "q" ]; then
		exit 0
	fi
}



# Main body of program, enter infinite loop prompting user for input on functionality theyd like to achieve.
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
