export class ErrorResource {

  constructor(public timestamp: Date, public correlationId: string,
              public description: string, public details: string ) {}
}
