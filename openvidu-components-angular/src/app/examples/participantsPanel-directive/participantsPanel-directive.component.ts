import { Component, OnDestroy, OnInit,  } from '@angular/core';
import { ParticipantAbstractModel, ParticipantService, TokenModel } from 'openvidu-angular';
import { Subscription } from 'rxjs';
import { RestService } from 'src/app/services/rest.service';

@Component({
	selector: 'app-participantsPanel-directive',
	template: `
		<ov-videoconference (onJoinButtonClicked)="onJoinButtonClicked()" [tokens]="tokens" [toolbarDisplaySessionName]="false">
			<div *ovParticipantsPanel id="my-panel">
				<ul id="local">
					<li>{{localParticipant.nickname}}</li>
				</ul>

				<ul id="remote">
					<li *ngFor="let p of remoteParticipants">{{p.nickname}}</li>
				</ul>
			</div>
		</ov-videoconference>
	`,
	styles: [
		`
			#my-panel {
				background: #faff7f;
				height: 100%;
				overflow: hidden;
			}
			#my-panel > #local {
				background: #a184ff;
			}
			#my-panel > #remote {
				background: #7fb8ff;
			}
		`
	]
})
export class ParticipantsPanelDirectiveComponent implements OnInit, OnDestroy {
	tokens: TokenModel;
	sessionId = 'participants-panel-directive-example';
	OPENVIDU_URL = 'https://localhost:4443';
	OPENVIDU_SECRET = 'MY_SECRET';
	localParticipant: ParticipantAbstractModel;
	remoteParticipants: ParticipantAbstractModel[];
	localParticipantSubs: Subscription;
	remoteParticipantsSubs: Subscription;

	constructor(
		private restService: RestService,
		private participantService: ParticipantService
	) {}

	ngOnInit(): void {
		this.subscribeToParticipants();
	}

	ngOnDestroy() {
		this.localParticipantSubs.unsubscribe();
		this.remoteParticipantsSubs.unsubscribe();
	}

	async onJoinButtonClicked() {
		this.tokens = {
			webcam: await this.restService.getToken(this.sessionId, this.OPENVIDU_URL, this.OPENVIDU_SECRET),
			screen: await this.restService.getToken(this.sessionId, this.OPENVIDU_URL, this.OPENVIDU_SECRET)
		};
	}

	subscribeToParticipants() {
		this.localParticipantSubs = this.participantService.localParticipantObs.subscribe((p) => {
			this.localParticipant = p;
		});

		this.remoteParticipantsSubs = this.participantService.remoteParticipantsObs.subscribe((participants) => {
			this.remoteParticipants = participants;
		});
	}
}
